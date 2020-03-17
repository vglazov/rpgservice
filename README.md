# Sample queries

### Add character
POST http://localhost:8080/characters?name=Captain%20America

### Get character info
GET http://localhost:8080/characters/1

### Add experience to character
POST http://localhost:8080/characters/1/add-experience?exp=950