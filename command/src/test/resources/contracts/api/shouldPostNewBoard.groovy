
org.springframework.cloud.contract.spec.Contract.make {

  request {

    method 'POST'

    url( '/boards/' )

  }

  response {

    status 201

    headers {
      header( 'LOCATION','http://localhost/boards/11111111-90ab-cdef-1234-567890abcdef' )
    }

  }

}
