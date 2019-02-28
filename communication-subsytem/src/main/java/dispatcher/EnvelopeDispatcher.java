package dispatcher;

import communicators.Envelope;
import communicators.EnvelopeReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnvelopeDispatcher<T> implements Runnable {
    private Map<Class<?>, List<Consumer<?>>> typeToConsumerMap = new HashMap<>();
    private Function<T, Object> decoder;
    private EnvelopeReceiver envelopeReceiver;

    public EnvelopeDispatcher(EnvelopeReceiver envelopeReceiver, Function<T, Object> decoder) {
        this.envelopeReceiver = envelopeReceiver;
        this.decoder = decoder;
    }

    public <X> void registerForDispatch(Class<X> clazz, Consumer<Envelope<X>> consumer) {
        List<Consumer<?>> list = typeToConsumerMap.getOrDefault(clazz, new ArrayList<>());
        list.add(consumer);
        typeToConsumerMap.put(clazz, list);
    }

    public void dispatch(Envelope<T> envelope) {
        if(envelope == null) {
            return;
        }

        Object decodedObj;

        try {
            decodedObj = decoder.apply(envelope.getMessage());
        } catch(Exception ignored) {
            return;
        }

        List<Consumer<?>> methodsToDispatch = typeToConsumerMap.getOrDefault(decodedObj.getClass(), new ArrayList<>());
        for(Consumer consumer : methodsToDispatch){
            consumer.accept(new Envelope<>(decodedObj, envelope.getSourceInetSocketAddress()));
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                dispatch(envelopeReceiver.receive());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
