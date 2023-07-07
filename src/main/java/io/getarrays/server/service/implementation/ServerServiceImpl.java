package io.getarrays.server.service.implementation;

import io.getarrays.server.model.Server;
import io.getarrays.server.repo.ServerRepo;
import io.getarrays.server.service.ServerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import static io.getarrays.server.enumeration.Status.SERVER_DOWN;
import static io.getarrays.server.enumeration.Status.SERVER_UP;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepo serverRepo;
    @Override
    public Server create(Server server) {
        log.info("Saving new server : {}",server.getName());
        server.setImageUrl(SetServetImageUrl());
        return serverRepo.save(server);
    }


    @Override
    public Server ping(String ipAdress) throws IOException {
        log.info("Pinging Server Ip : {}", ipAdress);
        Server server = serverRepo.findByIpAdress(ipAdress);
        InetAddress adress = InetAddress.getByName(ipAdress);
        server.setStatus(adress.isReachable(10000) ? SERVER_UP:SERVER_DOWN);
        serverRepo.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepo.findAll(PageRequest.of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by id : {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server : {}",server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by Id : {}",id);
        serverRepo.deleteById(id);
        return true;
    }

    private String SetServetImageUrl() {
        String[] imageNames = {"server1.png","server2.png","server3.png","server4.png"};
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/"
                + imageNames[new Random().nextInt(4)]).toUriString();
    }
}
