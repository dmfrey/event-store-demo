

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should publish board initialized event'

    label 'should_publish_board_initialized'

    input {

        triggeredBy('shouldPublishBoardInitialized()' )

    }

    outputMessage {

        sentTo 'board-event-notifications'

        body(
            file( 'json/board_initialized_event.json' )
        )

        headers {
            messagingContentType( applicationJson() )
        }

    }

}