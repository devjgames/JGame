package org.game;

import java.util.HashSet;

public class ResourceManager extends Resource {
    
    private final HashSet<Resource> resources = new HashSet<>();

    public <T extends Resource> T manage(T resource) {
        resources.add(resource);

        return resource;
    }
    
    public void unManage(Resource resource) {
        resources.remove(resource);
        resource.destroy();
    }

    public void clear() {
        for(Resource resource : resources) {
            resource.destroy();
        }
        resources.clear();
    }

    @Override
    public void destroy() {
        clear();
        super.destroy();
    }
}
