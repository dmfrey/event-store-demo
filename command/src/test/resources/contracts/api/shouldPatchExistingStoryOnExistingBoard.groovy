package api

org.springframework.cloud.contract.spec.Contract.make {

  request {
    method 'PUT'
    url( '/boards/11111111-90ab-cdef-1234-567890abcdef/stories/10240df9-4a1e-4fa4-bbd1-0bb33d764603' ) {

      queryParameters {

        parameter 'name': 'Updated Story Name'
      }

    }

  }

  response {

    status 202

  }

}
