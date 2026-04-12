# Agrosys
Proyecto para la digitalización de procesos de agricultura

## Como ejecutar el programa
- Para Ejecutar todo y construir [^1]
  ```
  docker-compose -f docker-compose.dev.yml up --build
  ```

- Reconstruir solo un contenedor (Ejemplo con agrosys-web)
  ```
  docker-compose -f docker-compose.dev.yml build agrosys-web
  ```
  
-  Reconstruir sin usar caché(Ejemplo con agrosys-auth) [^2]
	```
   docker-compose -f docker-compose.dev.yml build --no-cache agrosys-auth
   docker-compose -f docker-compose.dev.yml up -d agrosys-auth
    ```
 
- Iniciar contenedores si los detubiste con _pausa_ (Ejemplo con agrosys-web)
  ```
  docker-compose -f docker-compose.dev.yml start
  ```
[^1]: Siempre al arrancar el sistema.
[^2]: Solo haz build si cambias dependencias en pom.xml, no es indispensable despues de cada cambio.

## Como detener el programa
- Para detener todo [^3]
  ```
  docker-compose -f docker-compose.dev.yml down
  ```
- Para poner todo en pausa [^4]
  ```
  docker-compose -f docker-compose.dev.yml stop
  ```

  [^3]: Siempre al terminar de trabajar.
  [^4]: Más rapido de levantar de nuevo, pero sigue consumiendo un poco de espacio.

## Información Para Diseño
  - https://getbootstrap.com/docs/5.3/getting-started/introduction/ [^5]
  - https://bootswatch.com/sandstone/ [^6]

  [^5]: GUIA DE APOYO SOBRE BOOTSRAP.
  [^6]: LA VERSION INSTALADA EN EL PROYECTO.
