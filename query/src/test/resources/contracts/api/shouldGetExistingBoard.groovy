package api

org.springframework.cloud.contract.spec.Contract.make {

  request {

    method 'GET'

    url( '/boards/11111111-90ab-cdef-1234-567890abcdef' )

  }

  response {

    status 200

    body([
            name: "New Board",
            backlog: null
    ])

    headers {
      contentType( applicationJsonUtf8() )
    }

    bodyMatchers {

      jsonPath( '$.name', byEquality() )
//      jsonPath( '$.backlog', byNull() )
    }

  }

}
