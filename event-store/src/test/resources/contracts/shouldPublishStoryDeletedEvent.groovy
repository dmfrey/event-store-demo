

import org.springframework.cloud.contract.spec.Contract

Contract.make {

    description 'should publish story deleted event'

    label 'should_publish_story_deleted'

    input {

        triggeredBy('shouldPublishStoryDeleted()' )

    }

    outputMessage {

        sentTo 'board-event-notifications'

        body(
            file( 'json/story_deleted_event.json' )
        )

        headers {
            messagingContentType( applicationJson() )
        }

    }

}