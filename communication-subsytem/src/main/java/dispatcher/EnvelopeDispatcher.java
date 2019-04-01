package dispatcher;

import communicators.Envelope;
import messages.Message;

import java.util.*;
import java.util.function.Consumer;

public abstract class EnvelopeDispatcher {
    protected Map<Class<?>, List<Consumer<?>>> typeToConsumerMap = new HashMap<>();
    private Set<UUID> seenConversations = new HashSet<>();

    public <X> void registerForDispatch(Class<X> clazz, Consumer<Envelope<X>> consumer) {
        List<Consumer<?>> list = typeToConsumerMap.getOrDefault(clazz, Collections.synchronizedList(new ArrayList<>()));
        list.add(consumer);
        typeToConsumerMap.put(clazz, list);
    }

    protected void dispatch(Envelope<byte[]> envelope) {
        if(envelope == null) {
            return;
        }

        Message decodedMsg;

        try {
            decodedMsg = Message.decode(envelope.getMessage());
        } catch(Exception ignored) {
            return;
        }

        if(seenConversations.contains(decodedMsg.getConversationId())){
            return;
        }

        seenConversations.add(decodedMsg.getConversationId());

        List<Consumer<?>> methodsToDispatch = typeToConsumerMap.getOrDefault(decodedMsg.getClass(), new ArrayList<>());
        for(Consumer consumer : methodsToDispatch){
            consumer.accept(new Envelope<>(decodedMsg, envelope.getSourceInetSocketAddress()));
        }
    }
}
