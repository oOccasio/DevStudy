package chuchu.miniproject.service.worker;

import chuchu.miniproject.domain.Role;
import chuchu.miniproject.domain.Team;
import chuchu.miniproject.domain.worker.WorkList;
import chuchu.miniproject.domain.worker.Worker;
import chuchu.miniproject.dto.worker.request.RequestGoWorker;
import chuchu.miniproject.dto.worker.request.RequestLeaveWorker;
import chuchu.miniproject.dto.worker.request.RequestSaveWorker;
import chuchu.miniproject.dto.worker.request.RequestUsingDayOff;
import chuchu.miniproject.dto.worker.response.ResponseGetWorkList;
import chuchu.miniproject.dto.worker.response.ResponseGetWorker;
import chuchu.miniproject.dto.worker.response.ResponseListGetWorkList;
import chuchu.miniproject.repository.team.TeamRepository;
import chuchu.miniproject.repository.worker.WorkListRepository;
import chuchu.miniproject.repository.worker.WorkerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final TeamRepository teamRepository;
    private final WorkListRepository workListRepository;


    @Transactional
    public void saveWorker(RequestSaveWorker requestSaveWorker){
        String name = requestSaveWorker.getName();
        String teamName = requestSaveWorker.getTeamName();
        Role role = requestSaveWorker.getRole();
        LocalDate birthday = requestSaveWorker.getBirthday();
        LocalDate workStartDate = requestSaveWorker.getWorkStartDate();
        Team team = teamRepository.findByName(teamName).
                orElseThrow(() -> new EntityNotFoundException(String.format("존재하지 않는 팀이름 입니다: %s", teamName)));

        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("이름은 필수항목입니다.");
        }

        if(teamName == null || teamName.trim().isEmpty()){
            throw new IllegalArgumentException("팀이름은 필수항목입니다.");
        }

        if(role != Role.MANAGER && role != Role.WORKER){
            throw new IllegalArgumentException("유효하지 않은 역할입니다: " + role);
        }
        if(role == Role.MANAGER && team.getManager() != null){
            throw new IllegalArgumentException("이미 매니저가 있는 팀입니다.");
        }
        if(role == Role.MANAGER) {
            team.updateManager(name);
        }
        if(birthday.isAfter(LocalDate.now()) || birthday.isBefore(LocalDate.of(1900, 1, 1))){
            throw new IllegalArgumentException("유효한 생년월일을 입력해주세요");
        }


        if(workStartDate.isAfter(LocalDate.now()) || workStartDate.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new IllegalArgumentException("유효한 입사일을 입력해주세요");
        }

        Integer dayOff = Year.now().equals(Year.from(workStartDate)) ? 11 : 15;


        Worker worker = Worker.builder()
                .name(requestSaveWorker.getName())
                .teamName(requestSaveWorker.getTeamName())
                .role(requestSaveWorker.getRole())
                .birthday(requestSaveWorker.getBirthday())
                .workStartDate(requestSaveWorker.getWorkStartDate())
                .team(team)
                .dayOff(dayOff)
                .build();

        team.addWorker(worker);
    }

    @Transactional(readOnly = true)
    public List<ResponseGetWorker> getWorkers(){
        return workerRepository.findAll().stream()
                .map(worker -> ResponseGetWorker.builder()
                                .name(worker.getName())
                                .teamName(worker.getTeamName())
                                .role(worker.getRole())
                                .birthday(worker.getBirthday())
                                .workStartDate(worker.getWorkStartDate())
                                .build()).collect(Collectors.toList());
    }


    @Transactional
    public void goWorker(RequestGoWorker requestGoWorker){

        LocalTime goTime = requestGoWorker.goTime();
        Long currentId = requestGoWorker.workerId();
        Worker currentWorker = workerRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 Id 입니다."));

        LocalDate workDate = requestGoWorker.workDate();

        if(workListRepository.findByWorkerIdAndWorkDate(currentId, workDate).isPresent())
            throw new IllegalArgumentException("이미 출근한 직원입니다.");



        if(workDate == null){
            throw new IllegalArgumentException("유효한 날짜를 입력해주세요.");
        }

        if(workDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("미래 날짜는 입력할 수 없습니다.");
        }

        if(workDate.isBefore(currentWorker.getWorkStartDate())){
            throw new IllegalArgumentException("입사일 이전의 날짜는 입력할 수 없습니다.");
        }

        if(goTime == null){
            throw new IllegalArgumentException("유효한 출근시간을 입력해주세요.");
        }




        WorkList workList = WorkList.builder()
                .worker(currentWorker)
                .workDate(workDate)
                .goTime(goTime)
                .build();

        workListRepository.save(workList);

    }


    @Transactional
    public void leaveWorker(RequestLeaveWorker requestLeaveWorker){

        WorkList currentWorkList = workListRepository
                .findByWorkerIdAndWorkDate(requestLeaveWorker.workerId(), requestLeaveWorker.workDate())
                .orElseThrow(() -> new EntityNotFoundException("출근 리스트에 없는 직원입니다."));

        if(requestLeaveWorker.endTime() == null)
            throw new IllegalArgumentException("유효한 퇴근 시간을 입력해주세요.");

        currentWorkList.leaveWork(requestLeaveWorker.endTime());
        currentWorkList.worKingMinutes(currentWorkList);

    }


    @Transactional(readOnly = true)
    public ResponseGetWorkList getWorkList(Long workerId, YearMonth yearMonth){



        List<WorkList> workLists = workListRepository.findAll().stream()
                        .filter(workList -> workList.getWorker().getId().equals(workerId))
                        .filter(workList -> YearMonth.from(workList.getWorkDate()).equals(yearMonth))
                        .toList();



        if(workLists.isEmpty())
            throw new IllegalArgumentException("존재하지 않는 직원 Id 입니다.");



        Long sum = workLists.stream().mapToLong(WorkList::getWorkingMinutes).sum();

        List <ResponseListGetWorkList> responseListGetWorkLists = workLists.stream()
                .map(workList -> new ResponseListGetWorkList(workList.getWorkDate(), workList.getWorkingMinutes(), false))
                .toList();


        return new ResponseGetWorkList(responseListGetWorkLists, sum);


    }

    public void usingDayOff(RequestUsingDayOff requestUsingDayOff){
        Worker currentWorker = workerRepository.findById(requestUsingDayOff.workerId())
                .orElseThrow(()->new EntityNotFoundException("존재하지 않는 직원입니다."));

        LocalDate dayOffDay = requestUsingDayOff.dayOffDay();

        if(dayOffDay == null || LocalDate.now().isBefore(requestUsingDayOff.dayOffDay()))
            throw new IllegalArgumentException("유효한 연차일을 입력해주세요.");

        if(currentWorker.getDayOff() <= 0)
            throw new IllegalArgumentException("사용할 수 있는 연차가 존재하지 않습니다.");

        WorkList workList = WorkList.builder()
                .workingMinutes(0L)
                .workDate(dayOffDay)
                .usingDayOff(true)
                .build();

        currentWorker.useDayOff();
        workListRepository.save(workList);
    }

}
