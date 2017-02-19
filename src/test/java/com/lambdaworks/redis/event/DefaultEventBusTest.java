/*
 * Copyright 2011-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lambdaworks.redis.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ArrayBlockingQueue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

/**
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultEventBusTest {

    @Mock
    private Event event;

    @Test
    public void publishToSubscriber() throws Exception {

        EventBus sut = new DefaultEventBus(Schedulers.immediate());

        StepVerifier.create(sut.get()).then(() -> sut.publish(event)).expectNext(event).thenCancel().verify();
    }

    @Test
    public void publishToMultipleSubscribers() throws Exception {

        EventBus sut = new DefaultEventBus(Schedulers.immediate());

        ArrayBlockingQueue<Event> arrayQueue = new ArrayBlockingQueue<>(5);

        Disposable disposable1 = sut.get().doOnNext(arrayQueue::add).subscribe();
        StepVerifier.create(sut.get().doOnNext(arrayQueue::add)).then(() -> sut.publish(event)).expectNext(event).thenCancel()
                .verify();

        assertThat(arrayQueue.take()).isEqualTo(event);
        assertThat(arrayQueue.take()).isEqualTo(event);
        disposable1.dispose();
    }
}
