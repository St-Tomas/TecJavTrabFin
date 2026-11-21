# ScaffoldJ — Sistema de Gestión de Ferretería

**Trabajo Práctico Final – Tecnología Java (UNLZ – CREUS)**

## Integrantes
* Strevensky Tomas
* Pistiner Lambrecht Agustín

---

## Descripción

**ScaffoldJ** es un micro-framework servidor desarrollado íntegramente en Java. El framework gestiona las conexiones TCP, la concurrencia mediante un hilo por cliente, el parseo del protocolo de texto, el ruteo automático hacia controladores sin cadenas de `if`, la validación reutilizable de parámetros, el manejo global de excepciones, el contenedor de servicios compartidos y las sesiones con control de acceso por roles.

La aplicación que lo implementa es un **Sistema de Gestión de Ferretería** por consola: permite a los clientes loguearse, consultar el catálogo de herramientas, materiales y verificar stock, mientras que los administradores poseen permisos exclusivos para dar de alta nuevos productos, modificar precios y actualizar existencias.

---

## Cómo levantar el servidor y probar la aplicación

1. Abrir el proyecto en **Eclipse** o **VS Code**.
2. Ejecutar `app.Main`: el servidor inicia escuchando en el puerto **`6776`** por defecto .
3. Ejecutar una o más instancias en paralelo de `main.ClienteConsola` para interactuar con el servidor desde múltiples clientes simultáneos.

### Usuarios precargados para pruebas

| Usuario | Contraseña | Rol |
| :--- | :--- | :--- |
| `admin` | `1234` | `ADMINISTRADOR` |
| `tomas` | `1234` | `CLIENTE` |
| `pity` | `1234` | `CLIENTE` |

---

## Protocolo de Comunicación

* **Formato de Solicitud:**
  * Sin parámetros: `recurso/accion`
  * Con parámetros: `recurso/accion/param1/param2/...`
  * Para cerrar la conexión: `salir`
* **Formato de Respuesta:**
  * Éxito: `OK| <mensaje o resultado>`
  * Error: `ERROR| <descripción del error>`

### Recursos y acciones de la aplicación

| Recurso | Acción | Parámetros | Rol mínimo | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `auth` | `login` | `usuario/contraseña` | `INVITADO` | Autentica al cliente en la sesión actual |
| `auth` | `perfil` | *(ninguno)* | `CLIENTE` | Devuelve el usuario y rol activos |
| `auth` | `logout` | *(ninguno)* | `CLIENTE` | Cierra la sesión y vuelve a `INVITADO` |
| `catalogo` | `listar` | *(ninguno)* | `CLIENTE` | Lista todos los productos de la ferretería |
| `catalogo` | `buscar` | `texto` | `CLIENTE` | Filtra productos por código, nombre o categoría |
| `catalogo` | `stock` | `codigo` | `CLIENTE` | Muestra disponibilidad, unidades y precio |
| `inventario` | `alta` | `codigo/nombre/categoria/precio/stock` | `ADMINISTRADOR` | Da de alta un nuevo producto |
| `inventario` | `precio` | `codigo/nuevoPrecio` | `ADMINISTRADOR` | Modifica el precio de un producto existente |
| `inventario` | `stock` | `codigo/delta` | `ADMINISTRADOR` | Suma o resta unidades al stock (admite negativos) |

### Ejemplos de uso

```text
> catalogo/listar
Servidor: ERROR| Acceso denegado. Necesitas el rol CLIENTE (rol actual: INVITADO)

> auth/login/tomas/1234
Servidor: OK| Bienvenido Tomas (rol: CLIENTE)

> catalogo/buscar/taladro
Servidor: OK| [TALA-03] Taladro Percutor 650W (Electricas) - $89900.00 - Stock: 4

> catalogo/stock/MART-01
Servidor: OK| MART-01 - Martillo | DISPONIBLE (12 u.) | $14500.0

> inventario/precio/MART-01/18000
Servidor: ERROR| Acceso denegado. Necesitas el rol ADMINISTRADOR (rol actual: CLIENTE)

> auth/login/admin/1234
Servidor: OK| Bienvenido admin (rol: ADMINISTRADOR)

> inventario/alta/AMOL-06/Amoladora Angular 750W/Electricas/62500/8
Servidor: OK| Producto creado: [AMOL-06] Amoladora Angular 750W (Electricas) - $62500.00 - Stock: 8

> inventario/precio/MART-01/18000
Servidor: OK| Precio actualizado: [MART-01] Martillo (Herramientas) - $18000.00 - Stock: 12

> inventario/stock/MART-01/-2
Servidor: OK| Stock actualizado: [MART-01] Martillo (Herramientas) - $18000.00 - Stock: 10

> salir
Servidor: OK|Desconectado
```
---

## Declaracion de uso de Inteligencia Artificial

* En el desarrollo de este trabajo final de la materia **Tecnologia Java** estuvo presente el uso de inteligencia artifial, especificamente del modelo **Gemini**, para consultas sobre maneras de realizar codigo, funciones, una herramienta de testeo para la aplicacion y la documentacion para lograr una estetica acorde a una entrega de Trabajo Final.

* **Pruebas de integración automatizadas (`main.TestIntegracion`):** Se incluyó una clase de prueba automatizada desarrollada con el modelo de IA "Gemini 4 Neon" con el único propósito de testear todas las funcionalidades posibles del sistema, que levanta dos clientes concurrentes contra el servidor en ejecución, verifica 34 escenarios (errores de parser, roles, validadores, alta/modificación/stock compartido entre hilos, logout y aislamiento de sesiones) y genera un registro de comandos y respuestas en `test_log.txt`.
  * Para ejecutarla: iniciar `app.Main` y luego correr `main.TestIntegracion`.
