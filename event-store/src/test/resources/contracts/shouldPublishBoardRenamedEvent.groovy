

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should publish board renamed event'

    label 'should_publish_board_renamed'

    input {

        triggeredBy('shouldPublishBoardRenamed()' )

    }

    outputMessage {

        sentTo 'board-event-notifications'

        body(
            file( 'json/board_renamed_event.json' )
        )

        headers {
            messagingContentType( applicationJson() )
        }

    }

}