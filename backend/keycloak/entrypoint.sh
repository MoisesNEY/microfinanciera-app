#!/bin/bash
set -e

export DATA_DIR="/opt/keycloak/data"

echo "📥 Importando realm antes de iniciar Keycloak..."

# Función de reintento para la importación
import_realm() {
    local max_retries=5
    local count=0
    local success=false

    echo "📥 Intentando importar realm..."
    
    while [ $count -lt $max_retries ]; do
        if /opt/keycloak/bin/kc.sh import --dir "$DATA_DIR" --override true; then
            echo "✅ Import completado exitosamente."
            success=true
            break
        else
            echo "⚠️ Falló la importación (intento $((count+1))/$max_retries). Postgres podría estar reiniciando. Reintentando en 5s..."
            sleep 5
            count=$((count+1))
        fi
    done

    if [ "$success" = false ]; then
        echo "❌ ERROR CRÍTICO: La importación falló después de $max_retries intentos. Keycloak iniciará pero podría faltar la configuración."
    fi
}

# Función para realizar respaldos automáticos cada hora
auto_backup_service() {
    echo "⏳ [Auto-Backup] Servicio iniciado. Primera copia en 1 hora..."
    while true; do
        # Esperar 1 hora (3600 segundos)
        sleep 3600
        
        echo "⏰ [Auto-Backup] Ejecutando exportación periódica..."
        if /opt/keycloak/bin/kc.sh export --dir "$DATA_DIR" --users realm_file --optimized; then
            echo "✅ [Auto-Backup] Configuración guardada exitosamente."
        else
            echo "⚠️ [Auto-Backup] Error al exportar. La base de datos podría estar ocupada."
        fi
    done
}

import_realm

# Iniciar servicio de respaldo en segundo plano
auto_backup_service &
BACKUP_PID=$!

cleanup() {
    # Detener el servicio de backup
    kill $BACKUP_PID 2>/dev/null
    
    echo "🛑 Deteniendo Keycloak... Intentando exportar configuraciones."
    echo "⚠️ ADVERTENCIA: Si Postgres se apaga antes que este proceso termine, la exportación fallará."
    
    if /opt/keycloak/bin/kc.sh export --dir "$DATA_DIR" --users realm_file --optimized; then
        echo "✨ Export completado exitosamente."
    else
        echo "❌ Falló la exportación (probablemente Postgres ya se cerró)."
    fi
}

trap cleanup SIGTERM

echo "🚀 Iniciando Keycloak..."
/opt/keycloak/bin/kc.sh start-dev &

KC_PID=$!
wait $KC_PID
cleanup
