package com.bk.bkskup3.jsonstore.jsonflat.pancake;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/23/2015
 * Time: 10:24 PM
 */
public class PancakeNodeFactory {

    interface PancakeTypeFactory {
        PancakeValueType getHandledType();

        boolean canHandleNode(JsonNode node);

        JsonNode createNode(String value);
    }

    static class BoolPancakeTypeFactory implements PancakeTypeFactory {
        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.BOOL;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isBoolean();
        }

        @Override
        public JsonNode createNode(String value) {
            return BooleanNode.valueOf(Boolean.parseBoolean(value));
        }
    }

    static class IntPancakeTypeFactory implements PancakeTypeFactory {
        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.INT;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isInt();
        }

        @Override
        public JsonNode createNode(String value) {
            return IntNode.valueOf(Integer.parseInt(value));
        }
    }

    static class LongPancakeTypeFactory implements PancakeTypeFactory {
        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.LONG;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isLong();
        }

        @Override
        public JsonNode createNode(String value) {
            return LongNode.valueOf(Long.parseLong(value));
        }
    }

    static class TextPancakeTypeFactory implements PancakeTypeFactory {
        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.TEXT;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isTextual();
        }

        @Override
        public JsonNode createNode(String value) {
            return TextNode.valueOf(value);
        }
    }

    static class DoublePancakeTypeFactory implements PancakeTypeFactory {

        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.DBL;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isDouble();
        }

        @Override
        public JsonNode createNode(String value) {
            return DoubleNode.valueOf(Double.parseDouble(value));
        }
    }

    static class NullPancakeTypeFactory implements PancakeTypeFactory {

        @Override
        public PancakeValueType getHandledType() {
            return PancakeValueType.NULL;
        }

        @Override
        public boolean canHandleNode(JsonNode node) {
            return node.isNull();
        }

        @Override
        public JsonNode createNode(String value) {
            return NullNode.getInstance();
        }
    }

    private static final PancakeTypeFactory[] _typeFactories = {
            new BoolPancakeTypeFactory(),
            new DoublePancakeTypeFactory(),
            new TextPancakeTypeFactory(),
            new IntPancakeTypeFactory(),
            new NullPancakeTypeFactory(),
            new LongPancakeTypeFactory()
    };

    public PancakeNodeFactory() {

    }

    public PancakeValue createValue(JsonNode n) {
        PancakeTypeFactory typeFactory = findTypeFactory(n);
        return new PancakeValue(typeFactory.getHandledType(), n.asText());
    }

    public JsonNode createNode(PancakeValue value) {
        PancakeTypeFactory typeFactory = findTypeFactory(value.getType());
        return typeFactory.createNode(value.getValue());
    }

    private PancakeTypeFactory findTypeFactory(PancakeValueType valueType) {
        for (PancakeTypeFactory factory : _typeFactories) {
            if (factory.getHandledType() == valueType) {
                return factory;
            }
        }
        return null;
    }


    private PancakeTypeFactory findTypeFactory(JsonNode node) {
        for (PancakeTypeFactory factory : _typeFactories) {
            if (factory.canHandleNode(node)) {
                return factory;
            }
        }
        return null;
    }

}
