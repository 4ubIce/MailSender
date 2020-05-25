# Mail sender (using swing gui, regular expressions)

## Author

**Kirill Kupriyanov**

## Task

Task for using regular expressions and using third-party libraries.

The form for sending a message has the following fields:

1) the sender (FROM).
2) recipient (TO) or recipients (there may be several of them, then they are separated by commas).
3) subject of the email
4) message body (BODY)
5) the signature (FOOTER).
6) send button (SEND_BUTTON).

When you click the send button, you need to check the validity of entries in the fields, and if it is valid, send the message, otherwise highlight the field with the error in red and pass the input focus there:
1. Each sender and recipient can be recorded as just an e-mail address sender@example.com, and in the form of the name of the sender with the indication of e-mail in angle brackets Kirill <sender@example.com>. The e-mail entry may be correct. There can only be one sender.
2. Each recipient in the recipients (TO) field must meet the SENDER entry rules, but there may be many recipients separated by commas (or just one).
3. Subject can contain only letters of the Spanish, Latin (English), Russian, and Belarusian alphabets (and only lowercase ones), as well as spaces, numbers, punctuation, minus, underscores, and other characters from the readable ASCII part. In this case, the encoding of the email subject is UTF-8, as well as the other fields.
4. The body of the email (BODY) can contain any characters valid for UTF-8. But before sending, each subsequent word repeated in the text must be enclosed in the html strong tag (highlighted in bold).
5. The Signature to the message (FOOTER) is added to the end of the BODY before sending, with the characters with gray color.
6. After successful sending, all fields are cleared, except the SENDER and FOOTER fields.
7. To check (validate) fields in accordance with the specified rules, use the regular expression mechanism (java.util.regex). The rules are self-written, but you can read the documentation and search for tips on the Internet.
8. To send a message formed in this way, use a third-party library from spring-email (or another simple one, at your choice). Send using the SMTP Protocol.

## Starting

in the project folder

for run application use:
```
gradle run
```
for run application testing use:
```
gradle test
```