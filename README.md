# Project Title

Initial version.

Simple Cloud Storage JAX-RS HTTP REST Service backed by Redis key-value Store example.

The service core is the [the store package](https://github.com/mannito/rezt/tree/master/src/main/java/org/manny/rezt/store) and the [http endpoint resource entry point](https://github.com/mannito/rezt/tree/master/src/main/java/org/manny/rezt/resource/ReztFile.java)

### Endpoints

  All endpoints use JWT Token Bearer Authentication. They operate on whole files.

	- Upload file: multipart form-data POST (form-field=blob): /up/[?name=<FILE_NAME>][?tag=<TAG>...]

curl example:

```
		cat "/local/file" | curl -vi -F 'blob=@-' -H "Authorization: Bearer $API_TOKEN" http://$SERVER/up/"$@"
```


	- Download by name: GET: /dl/name/<FILE_NAME>/:name[?tag=<TAG>...]
	- Download by fileId: GET: /dl/id/<FILE_ID>/:fileid

curl examples:

 - download by name:

```
		curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/name/<FILE_NAME>
```

 - download by fileId:

```
		curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/id/<FILE_ID>
```

 - download by name, matching tags:

```
		curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/name/<FILE_NAME>?tag=<TAG>
```

## Built With

* [Dropwizard](http://www.dropwizard.io/) - Java REST framework

## Authors

* **Javier Barrio**
