# Instrucciones de Reconstrucción del Backend (Avanzado)

Se han ampliado significativamente los permisos del **Jefe de Cobranza** para otorgar autonomía total en la gestión de créditos, no solo en pagos.

### 1. Loan Microservice (Actualización Crítica)
Se modificaron:
- `LoanController`: Crear/Editar préstamos.
- `LoanProductController`: Ver productos de crédito (necesario para crear préstamos).
- `LoanApplicationController`: Gestionar solicitudes de crédito.

**Ruta:** `backend\services\loan-microservice`
**Comando:**
```powershell
mvn clean package -DskipTests
```
**Acción:** Detener instancia anterior y ejecutar el nuevo `.jar`.

### 2. Payment Microservice
Se modificó `PaymentController` para permitir creación y edición de pagos por parte del Jefe de Cobranza.
**Ruta:** `backend\services\payment-microservice`
**Comando:**
```powershell
mvn clean package -DskipTests
```
**Acción:** Reiniciar el servicio.

---
**Resultado esperado:**
El usuario `jefe_cobranza` podrá:
1. Entrar a la vista de "Cartera" y **Crear Nuevos Préstamos** (sin error al cargar productos).
2. Ver y gestionar **Solicitudes de Crédito**.
3. **Registrar Pagos** sin restricciones.
