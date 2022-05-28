import { UnavailabilityError } from 'expo-modules-core';

import TopicSubscribeModule from './TopicSubscribeModule';

export default async function topicUnsubscribeAsync(topic: string): Promise<void> {
  if (!TopicSubscribeModule.topicUnsubscribeAsync) {
    throw new UnavailabilityError('ExpoNotifications', 'topicUnsubscribeAsync');
  }

  return TopicSubscribeModule.topicUnsubscribeAsync(topic);
}
