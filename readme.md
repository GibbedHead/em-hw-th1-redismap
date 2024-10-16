# RedisMap

## Описание проекта

Проект **RedisMap** реализует класс `RedisMap`, который имплементирует интерфейс `Map` в Java, используя хранилище данных Redis. Данный класс позволяет работать с ключами и значениями в формате `String`, обеспечивая основные операции, такие как `put` и `get` и прочие.

### Задание

В рамках задания была поставлена задача:
- Реализовать класс `RedisMap`, который будет использовать Redis как хранилище данных.
- Имплементировать интерфейс `Map<String, String>`, включая базовые методы `put` и `get`.
- Написать тесты с использованием JUnit 5.
- Для подключения к Redis использовать библиотеку Jedis.
- Развернуть Redis в Docker.

## Структура репозитория

Репозиторий содержит следующие папки и файлы:

- **redismap/** - основная папка с реализацией класса `RedisMap`.
- **redismapdemo/** - папка с классом `Main`, который демонстрирует тестовое использование класса `RedisMap`. Здесь `RedisMap` подключен как зависимость.
- **compose.yml** - файл, содержащий конфигурацию для развертывания сервиса Redis на стандартном порту, необходимом для работы с проектом `redismapdemo`.

## Установка и запуск

### Требования

- Java 17 или выше
- Maven
- Docker

### Шаги по запуску

1. Клонируйте репозиторий:

   ```bash
   git clone https://github.com/GibbedHead/em-hw-th1-redismap.git
   cd em-hw-th1-redismap
   ```

2. Запустите Redis с помощью Docker:

   ```bash
   docker-compose -f compose.yml up -d
   ```
3. Для запуска тестов в проекте `redismap` используйте:

   ```bash
   cd redismap
   mvn test
   ```
   
4. Соберите проект:

   ```bash
   mvn clean install
   ```

5. Запустите демо приложение:

   ```bash
   cd ../redismapdemo
   mvn package
   java -jar .\target\redismapdemo-1.0-SNAPSHOT.jar
   ```

## Тестирование

Тесты для класса `RedisMap` написаны с использованием библиотеки Testcontainers, что позволяет изолированно тестировать взаимодействие с Redis.

## Использование

Класс `RedisMap` предоставляет стандартные методы для работы с ключами и значениями, такие как:

- `put(String key, String value)` - добавляет или обновляет значение по заданному ключу.
- `get(Object key)` - возвращает значение по заданному ключу.

Пример использования:

```java
RedisMap redisMap = new RedisMap(jedis);
redisMap.put("key1", "value1");
String value = redisMap.get("key1");
System.out.println(value); // вывод: value1
```