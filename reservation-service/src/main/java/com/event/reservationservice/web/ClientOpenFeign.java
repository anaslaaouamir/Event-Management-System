package com.event.reservationservice.web;

import com.event.reservationservice.models.Client;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.TimeUnit;

@FeignClient(name = "CLIENT-SERVICE")

public interface ClientOpenFeign {

    Cache<Long, Client> clientCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES) // Cache entries expire after 10 minutes
            .maximumSize(100) // Limit the cache size to 100 entries
            .build();

    @CircuitBreaker(name="reservationServiceCB", fallbackMethod = "getDefaultClient")
    @GetMapping("/clients/{id}")
    default Client getClient(@PathVariable Long id) {

        Client fetchedClient = fetchClientFromService(id);
        Client cachedClient = clientCache.getIfPresent(id);

        if (cachedClient == null) {
            clientCache.put(id, fetchedClient); // Store the client in cache
        }
        return fetchedClient;
    }

    // Direct call to the service
    @GetMapping("/clients/{id}")
    Client fetchClientFromService(@PathVariable Long id);

    default Client getDefaultClient(Long id, Throwable t) {

        System.out.println("**********************inside getDefaultClient*********");

        Client cachedClient = clientCache.getIfPresent(id);
        if (cachedClient != null) {
            return cachedClient;
        }
        // Return a default client object if no cache is available
        return null;
    }
}
