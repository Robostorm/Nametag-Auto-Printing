package org.robostorm.queue;

import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NameTagQueue {
    @Autowired
    private Config config;
    private List<NameTag> queue = new ArrayList<>();

    public void addToQueue(NameTag nameTag) throws IOException {
        queue.add(nameTag);
        config.saveQueue(this);
        System.out.println("Added Nametag to queue: " + nameTag);
        System.out.println("Queue: " + queue);
    }

    public void addAllToQueue(Collection<NameTag> nameTags) throws IOException {
        System.out.println("New Printers: " + nameTags);
        queue.addAll(nameTags);
        config.saveQueue(this);
        System.out.println("Added to queue: " + nameTags);
        System.out.println("Queue: " + queue);
    }

    public void removeFromQueue(NameTag nameTag) throws IOException {
        queue.remove(nameTag);
        config.saveQueue(this);
        System.out.println("Removed Nametag from queue: " + nameTag);
    }

    public void removeFromQueue(int id) throws IOException {
        for(NameTag nameTag : queue) {
            if (nameTag.getId() == id) {
                removeFromQueue(nameTag);
            }
        }
    }

    public void updateNameTag(NameTag nameTag) throws IOException {
        boolean found = false;
        if(nameTag.getConfig() == null)
            nameTag.setConfig(config);
        for(int i = 0 ; i < queue.size(); i++) {
            if(queue.get(i).getId() == nameTag.getId()) {
                queue.set(i, nameTag);
                found = true;
                config.saveQueue(this);
            }
        }
        if(!found) {
            addToQueue(nameTag);
        }
    }

    public void updateNameTag(NameTag oldNameTag, NameTag newNameTag) throws IOException {
        for(int i = 0 ; i < queue.size(); i++) {
            if(queue.get(i) == oldNameTag) {
                queue.set(i, newNameTag);
            }
        }
        config.saveQueue(this);
    }

    public List<NameTag> getAllNametags() {
        return queue;
    }

    public NameTag getNameTag(String name) {
        for(NameTag nameTag : queue)
            if(nameTag.toString().equals(name))
                return nameTag;
        return null;
    }

    public NameTag getNextNameTag() {
        for(int i = 0; i < queue.size() && queue.size() > 0; i++) {
            if(!queue.get(i).isSliced() || !queue.get(i).isGenerated() || !queue.get(i).isPrinting())
                return queue.get(i);
        }
        return null;
    }
}
