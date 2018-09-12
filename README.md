# Rezt HTTP Redis Store

Initial version.

Simple Cloud Storage JAX-RS HTTP REST Service backed by Redis key-value store demo.

The service core is the [the store package](https://github.com/mannito/rezt/tree/master/src/main/java/org/manny/rezt/store) and the [http endpoint resource controller](https://github.com/mannito/rezt/tree/master/src/main/java/org/manny/rezt/resource/ReztFile.java).

Uploaded files have a unique ID in each user namespace and they can have a name alias. Names act like flat directories and can have N fileIds linked to it. FileIds can have multiple tags, so names can be filtered by tags to extract specific fileIds.

### Endpoints

  All endpoints use JWT Token Bearer Authentication. They operate on whole files. They accept a trailing output modifier that affects response format: /h header file info (JSON) /d binary file (octet-stream) /a header+data (JSON), defaults to /a if ommited.


 * Upload file: multipart form-data POST (form-field=blob): /up/[?name=<FILE_NAME>][?tag=<TAG>...]

- curl example:

```

curl -vi -F 'blob=@-' -H "Authorization: Bearer $API_TOKEN" http://$SERVER/up/"$@" < "/local/file"

```

 * Download /dl/

	* Download by name: GET: /dl/name/<FILE_NAME>/[?tag=<TAG1>&tag=<TAG1>...&tag=<TAGN>]

	* Download by fileId: GET: /dl/id/<FILE_ID>/


 - Download by name:

```

curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/name/<FILE_NAME>


```

 - Download by fileId:

```

curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/id/<FILE_ID>


```

 - Download by name, matching tags:

```

curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/name/<FILE_NAME>?tag=<TAG>


```

 - Download by fileId, raw binary file response:

```

curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/id/<FILE_ID>/d

```

- Download by fileId, file stat (header) only response:

```

curl -vi -H "Authorization: Bearer $TOK" http://$SERVER/dl/id/<FILE_ID>/h

```

## TODO

* Very early and raw stage. Refactoring, restructuring and cleanups needed.
* Decouple Redis from store service.
* Pipeline Redis commands through requests.
* Extend and refine API endpoints.
* Automated tests.
* ...

## Built With

* [Dropwizard](https://www.dropwizard.io/) - Java REST framework
* [Redis](https://redis.io/) - In memory distributed key-value store.

## Author

* **Javier Barrio**
