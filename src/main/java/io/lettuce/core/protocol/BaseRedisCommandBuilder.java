/*
 * Copyright 2011-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.protocol;

import io.lettuce.core.RedisException;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.BooleanOutput;
import io.lettuce.core.output.CommandOutput;
import io.lettuce.core.output.IntegerOutput;
import io.lettuce.core.output.NestedMultiOutput;
import io.lettuce.core.output.ObjectOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.output.ValueOutput;

/**
 * @author Mark Paluch
 * @since 3.0
 */
public class BaseRedisCommandBuilder<K, V> {

    protected final RedisCodec<K, V> codec;

    public BaseRedisCommandBuilder(RedisCodec<K, V> codec) {
        this.codec = codec;
    }

    protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output) {
        return createCommand(type, output, (CommandArgs<K, V>) null);
    }

    protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output, K key) {
        CommandArgs<K, V> args = new CommandArgs<>(codec).addKey(key);
        return createCommand(type, output, args);
    }

    protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output, K key, V value) {
        CommandArgs<K, V> args = new CommandArgs<>(codec).addKey(key).addValue(value);
        return createCommand(type, output, args);
    }

    protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output, K key, V[] values) {
        CommandArgs<K, V> args = new CommandArgs<>(codec).addKey(key).addValues(values);
        return createCommand(type, output, args);
    }

    protected <T> Command<K, V, T> createCommand(CommandType type, CommandOutput<K, V, T> output, CommandArgs<K, V> args) {
        return new Command<>(type, output, args);
    }

    @SuppressWarnings("unchecked")
    protected <T> CommandOutput<K, V, T> newScriptOutput(RedisCodec<K, V> codec, ScriptOutputType type) {
        switch (type) {
            case BOOLEAN:
                return (CommandOutput<K, V, T>) new BooleanOutput<>(codec);
            case INTEGER:
                return (CommandOutput<K, V, T>) new IntegerOutput<>(codec);
            case STATUS:
                return (CommandOutput<K, V, T>) new StatusOutput<>(codec);
            case MULTI:
                return (CommandOutput<K, V, T>) new NestedMultiOutput<>(codec);
            case VALUE:
                return (CommandOutput<K, V, T>) new ValueOutput<>(codec);
            case OBJECT:
                return (CommandOutput<K, V, T>) new ObjectOutput<>(codec);
            default:
                throw new RedisException("Unsupported script output type");
        }
    }

}
