

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should publish story updated event'

    label 'should_publish_story_updated'

    input {

        triggeredBy('shouldPublishStoryUpdated()' )

    }

    outputMessage {

        sentTo 'board-event-notifications'

        body(
            file( 'json/story_updated_event.json' )
        )

        headers {
            messagingContentType( applicationJson() )
        }

    }

}