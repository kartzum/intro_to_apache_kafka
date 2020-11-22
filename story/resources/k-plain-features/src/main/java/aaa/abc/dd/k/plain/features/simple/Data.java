package aaa.abc.dd.k.plain.features.simple;

import java.util.List;

public class Data {
    public static class FeatureDescriptor {
        public final String source;
        public final String key;

        public FeatureDescriptor(String source, String key) {
            this.source = source;
            this.key = key;
        }
    }

    public static class FeaturesDescriptor {
        public final List<FeatureDescriptor> featureDescriptors;
        public final String sinkSource;

        public FeaturesDescriptor(List<FeatureDescriptor> featureDescriptors, String sinkSource) {
            this.featureDescriptors = featureDescriptors;
            this.sinkSource = sinkSource;
        }
    }
}
