package com.aqi.service;

import com.aqi.dto.aqi.AqiDataDto;
import com.aqi.entity.AqiDataPoint;
import com.aqi.exception.ResourceNotFoundException;
import com.aqi.repository.AqiDataPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AqiService {

    @Autowired
    private AqiDataPointRepository aqiDataPointRepository;

    public AqiDataDto getCurrentAqi(String city) {
        AqiDataPoint dataPoint = aqiDataPointRepository.findFirstByCityIgnoreCaseOrderByTimestampDesc(city);

        if (dataPoint == null) {
            throw new ResourceNotFoundException("No AQI data found for city: " + city);
        }

        return new AqiDataDto(
                dataPoint.getId(),
                dataPoint.getCity(),
                dataPoint.getAqiValue(),
                dataPoint.getTimestamp()
        );
    }

    public List<AqiDataDto> getAqiHistory(String city) {
        List<AqiDataPoint> dataPoints = aqiDataPointRepository.findByCityIgnoreCaseOrderByTimestampDesc(city);

        return dataPoints.stream()
                .map(dp -> new AqiDataDto(dp.getId(), dp.getCity(), dp.getAqiValue(), dp.getTimestamp()))
                .collect(Collectors.toList());
    }

    public List<AqiDataDto> getAqiHistory(String city, LocalDateTime startTime) {
        List<AqiDataPoint> dataPoints = aqiDataPointRepository.findByCityAndTimestampAfter(city, startTime);

        return dataPoints.stream()
                .map(dp -> new AqiDataDto(dp.getId(), dp.getCity(), dp.getAqiValue(), dp.getTimestamp()))
                .collect(Collectors.toList());
    }
}



