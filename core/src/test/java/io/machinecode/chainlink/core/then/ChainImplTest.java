/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.then.ResolvedChain;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainImplTest {

    @Test
    public void resolveTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<>();
        final ChainImpl<Void> second = new ChainImpl<>();
        final ChainImpl<Void> third = new ChainImpl<>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();
        
        second.resolve(null);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertEquals(1, done.getCount());

        first.resolve(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertEquals(1, done.getCount());

        third.resolve(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertEquals(1, done.getCount());

        third.link(new ResolvedChain<>(null));
        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }

    @Test
    public void rejectTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<>();
        final ChainImpl<Void> second = new ChainImpl<>();
        final ChainImpl<Void> third = new ChainImpl<>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();

        second.reject(null);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertEquals(1, done.getCount());

        first.reject(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertFalse(third.isDone());
        Assert.assertEquals(1, done.getCount());

        third.reject(null);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertEquals(1, done.getCount());

        third.link(new ResolvedChain<>(null));
        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }

    @Test
    public void cancelTest() throws Exception {
        final ChainImpl<Void> first = new ChainImpl<>();
        final ChainImpl<Void> second = new ChainImpl<>();
        final ChainImpl<Void> third = new ChainImpl<>();

        first.link(second);
        second.link(third);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertFalse(third.isDone());

        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    third.get(1, TimeUnit.SECONDS);
                } catch (final Exception e) {
                    // Swallow
                } finally {
                    done.countDown();
                }
            }
        }).start();
        latch.await();

        third.cancel(true);

        Assert.assertFalse(first.isDone());
        Assert.assertFalse(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertEquals(1, done.getCount());

        second.cancel(true);

        Assert.assertFalse(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertEquals(1, done.getCount());

        first.cancel(true);

        Assert.assertTrue(first.isDone());
        Assert.assertTrue(second.isDone());
        Assert.assertTrue(third.isDone());
        Assert.assertEquals(1, done.getCount());


        third.link(new ResolvedChain<>(null));
        done.await(2, TimeUnit.SECONDS);
        Assert.assertEquals(0, done.getCount());
    }
}
