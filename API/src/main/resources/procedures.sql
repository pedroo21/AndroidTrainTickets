# Get all stations
DELIMITER //
create procedure getStations()
  begin
    select id, stationNumber, stationName from stations;
  end //
DELIMITER ;


# Get schedule for the train given the DepartureStation and ArrivalStation
DELIMITER //
create procedure getSchedule(IN depStation INT,IN arrStation INT)
  begin
    if(depStation<arrStation) then
      select stb.startStation,stb.endStation,sch.id,sch.departureStationId,sch.arrivalStationId,sch.departureTime,sch.arrivalTime
      from
        (select tr.id,
           min(departureStationId) as departureStationId,
           max(arrivalStationId)as arrivalStationId,
           max(arrivalTime) as arrivalTime,
           min(departureTime)as departureTime
         from trips tr join steps st on tr.id=st.fkTrip
         where departureStationId=depStation or arrivalStationId=arrStation group by id) as sch
        join
        (select p1.id as st1,
                p1.stationName as startStation,
                p2.id as st2,
                p2.stationName as endStation
         from stations p1,stations p2 where p1.id=depStation and p2.id=arrStation) as stb
      where stb.st1=sch.departureStationId and stb.st2=sch.arrivalStationId order by departureTime;
    else
      select stb.startStation,stb.endStation,sch.id,sch.departureStationId,sch.arrivalStationId,sch.departureTime,sch.arrivalTime
      from
        (select tr.id,
           max(departureStationId) as departureStationId,
           min(arrivalStationId)as arrivalStationId,
           max(arrivalTime) as arrivalTime,
           min(departureTime)as departureTime
         from trips tr join steps st on tr.id=st.fkTrip
         where departureStationId=depStation or arrivalStationId=arrStation group by id) as sch
        join
        (select p1.id as st1,
                p1.stationName as startStation,
                p2.id as st2,
                p2.stationName as endStation
         from stations p1,stations p2 where p1.id=depStation and p2.id=arrStation) as stb
      where stb.st1=sch.departureStationId and stb.st2=sch.arrivalStationId order by departureTime;
    end if;
  end //
DELIMITER ;