# registry-service

Сервис-реестр HTTP-клиентов поставщиков. 
По id поставщика и номеру телефона проверяет, доступен ли номер.

Стек: Java 17, Spring Boot 3, RestClient, JUnit 5, Lombok.

При старте поставщики читаются из `suppliers.json` (путь задается в `application.yml`, параметр `registry.suppliers-file`).
Для каждого поставщика создается свой `SupplierClient`, они хранятся в `SupplierRegistryService` по id.

Проверка номера: сначала вызывается  `supplierRegistryService.isAvailable(supplierId, phone)`, затем - `GET {baseUrl}/subscribers/{phone}`.

- 200 и `{"status":"ok"}` - номер доступен
- 404 - номер недоступен
- остальные ошибки (HTTP и сетевые) не перехватываются и уходят в вызывающий код
- если поставщика с таким id нет - `IllegalArgumentException`

Набор поставщиков можно заменить целиком через `setSuppliers(...)`.

Есть интерфейс `SupplierProvider` и его реализация `JsonFileSupplierProvider` для чтения json-файла. 
В конфиге сейчас используется json-файл. Чтобы брать поставщиков из другой реализации SupplierProvider, достаточно поменять бин `supplierProvider` в `RegistryConfig`.
Если файл не найден или json некорректный, приложение не стартует (будет `SupplierLoadException`).

Пример файла:
```json
[
  {
    "id": 1,
    "baseUrl": "https://test1.ru/api",
    "apiKey": "key-1"
  }
]
```
