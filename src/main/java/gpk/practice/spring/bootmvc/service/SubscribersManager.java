package gpk.practice.spring.bootmvc.service;

import gpk.practice.spring.bootmvc.dto.MessageDto;
import gpk.practice.spring.bootmvc.model.LongPollSubscriber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SubscribersManager {
    private List<LongPollSubscriber> subscribers = new CopyOnWriteArrayList<>();
    public void addSubscriber(LongPollSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    private void removeSubscriber(LongPollSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void abortSubscriber(LongPollSubscriber subscriber) {
        subscriber.getResponse().setResult(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        removeSubscriber(subscriber);
    }

    public void broadcast(List<MessageDto> messages) {
        for (LongPollSubscriber subscriber : subscribers) {
            subscriber.getResponse().setResult(new ResponseEntity<>(messages, HttpStatus.OK));
            removeSubscriber(subscriber);
        }
    }
}
