

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should publish story added event'

    label 'should_publish_story_added'

    input {

        triggeredBy('shouldPublishStoryAdded()' )

    }

    outputMessage {

        sentTo 'board-event-notifications'

        body(
            file( 'json/story_added_event.json' )
        )

        headers {
            messagingContentType( applicationJson() )
        }

    }

}