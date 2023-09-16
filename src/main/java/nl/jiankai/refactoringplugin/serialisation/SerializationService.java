package nl.jiankai.refactoringplugin.serialisation;

public interface SerializationService {
    byte[] serialize(Object object);

    <T> T deserialize(byte[] object, Class<T> deserializedClass);
}
