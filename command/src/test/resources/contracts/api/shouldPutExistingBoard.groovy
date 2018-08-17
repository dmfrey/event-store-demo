
org.springframework.cloud.contract.spec.Contract.make {

  request {

    method 'PATCH'

    url( '/boards/11111111-90ab-cdef-1234-567890abcdef' ) {

      queryParameters {

        parameter 'name': 'New Name'
      }

    }

  }

  response {

    status 202

  }

}
