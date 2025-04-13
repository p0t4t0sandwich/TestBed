package dev.neuralnexus.bedapi;

public interface ResourceLocation {
    /**
     * Gets the path of the resource.
     *
     * @return The path of the resource
     */
    String value();

    /**
     * Gets the namespace of the resource.
     *
     * @return The namespace of the resource
     */
    String namespace();
}
