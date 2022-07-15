# Сервер генерации титров

### Использование

Получить видео можно отправив GET запрос на /create, в теле отправить:

```sh
{
	"text":["Текст1\n новая строка","Текст2 \n новая строка"],
	"centring":false,
	"offset":0,
	"font":"Roboto",
	"color":"#ffffff",
	"backgroundColor":"#ffffff",
	"textSize":32,
	"speed":2,
	"distance":32
}
```
Получить список шрифтов можно отправив GET запрос на /getAllFont,

Загрузить новый шрифт отправив POST запрос на /uploadFonts, в теле должен быть Multipart файл с расширением .ttf 

[Щрифты можно найти тут](https://fonts.google.com/)

