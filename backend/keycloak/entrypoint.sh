#!/bin/bash
set -e

export REALM_NAME="microfinance-microservice-realm"
export EXPORT_DIR="/opt/keycloak/data/import"

cleanup() {
    echo "🔄 Exportando cambios de Keycloak…"
    /opt/keycloak/bin/kc.sh export \
        --realm "$REALM_NAME" \
        --dir "$EXPORT_DIR" \
        --users realm_file \
        --optimized || true \
        --overwrite || true
    echo "✅ Exportación completa."
}

trap cleanup SIGTERM

echo "🚀 Iniciando Keycloak…"
/opt/keycloak/bin/kc.sh start-dev --import-realm &
KC_PID=$!

wait $KC_PID
cleanup
