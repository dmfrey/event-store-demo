
org.springframework.cloud.contract.spec.Contract.make {

  request {

    method 'POST'

    url( '/boards/11111111-90ab-cdef-1234-567890abcdef/stories' ) {

      queryParameters {

        parameter 'name': 'New Story Name'
      }

    }

  }

  response {

    status 201

    headers {
      header( 'LOCATION','http://localhost/boards/11111111-90ab-cdef-1234-567890abcdef/stories/10240df9-4a1e-4fa4-bbd1-0bb33d764603' )
    }

  }

}
