#!/bin/bash
set -e

export DATA_DIR="/opt/keycloak/data"

echo "📥 Importando realm antes de iniciar Keycloak..."

# Import único, directo desde /data
/opt/keycloak/bin/kc.sh import \
    --dir "$DATA_DIR" \
    --override true || true

echo "✅ Import completado."

cleanup() {
    echo "💾 Exportando cambios al mismo directorio..."
    /opt/keycloak/bin/kc.sh export \
        --dir "$DATA_DIR" \
        --users realm_file \
        --optimized || true
    echo "✨ Export completado (mismo archivo actualizado)."
}

trap cleanup SIGTERM

echo "🚀 Iniciando Keycloak..."
/opt/keycloak/bin/kc.sh start-dev &

KC_PID=$!
wait $KC_PID
cleanup
