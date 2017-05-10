# API - APP Model

App Model is the base model to desenv news microservices.
This document show how the programer must write docs about the services, ende-points and tec information about micro-service.

- Servlet 3.1.0 (http://java.net/projects/servlet-spec/pages/Home)
- JBoss RestEasy 3.0.19.Final (http://resteasy.jboss.org)
- Guice 3.0 (http://github.com/google/guice)

## Index
- [appModel](#appModel)

## AppModel
##### `End-point`: http://[server]:[port]/[path]/[to]/[your]/[service]

`Method`: [@Post,@Get,@Put,...]
`Description`: Short description about the service
`path`: /[path of method]
`Headers`:
|     Key       | Value         |
|:-------------:|:-------------:|
| [Header 1]  | [key 1] |
| [Header 2] | [key 2] |
| [Header X] | [key X] |

`body`:
```
{
	"prop_2":"X1",
	"prop_3":"X2",
	"prop_4":"X3",
	"prop_5":"X4",
	"prop_N":"XN"
}
```