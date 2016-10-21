# scouter-plugin-server-dump-thread
### Scouter server plugin to dump ThreadDumps

- 본 프로젝트는 스카우터 서버 플러그인으로써 java agent의 ProcessCpu가 정해진 시간동안 임계치를 초과할 경우 스레드 덤프 파일을 생성한다.

### Properties (스카우터 서버 설치 경로 하위의 conf/scouter.conf)
* **_ext\_plugin\_dump\_thread\_enable_** : Dump 기능 사용 여부 (true / false) - 기본 값은 false
* **_ext\_plugin\_dump\_thread\_debug_** : 로깅 여부 - 기본 값은 false
* **_ext\_plugin\_dump\_thread\_threshhold\_cpu_** : ProcessCpu의 임계치 값(%)
* **_ext\_plugin\_dump\_thread\_cpu\_high\_duration_** : ProcessCpu가 임계치를 초과하여 유지해야 하는 시간(초)
* **_ext\_plugin\_dump\_thread\_dump\_interval_** : Thread Dump 수행 주기(초)
* **_ext\_plugin\_dump\_thread\_dump\_count_** : ProcessCpu가 임계치를 초과하여 정해진 시간동안 유지될 때 최대로 생성되는 덤프 수

* Example
```
ext_plugin_dump_thread_enable=true
ext_plugin_dump_thread_debug=true
ext_plugin_dump_thread_threshhold_cpu=80
ext_plugin_dump_thread_cpu_high_duration=300
ext_plugin_dump_thread_dump_interval=5
ext_plugin_dump_thread_dump_count=3
```

### Dependencies
* Project
    - scouter.common
    - scouter.server
    
### Build & Deploy
* Build
    - mvn install을 수행한다.
    
* Deploy
    - 빌드 후 프로젝트 하위의 target 디렉토리에 scouter-plugin-server-dump-thread-0.0.1.jar 파일을 복사하여 스카우터 서버 설치 경로 하위의 lib/ 폴더에 저장한다.
    
* Requirements
    - Scouter (Java) Agent 설치 디렉토리 하위에 dump 디렉토리가 생성되어 있어야 한다.
    - 자바 프로세스의 ProcessCpu 정보를 확인하기 위해 Scouter Host Agent가 함께 구동되어야 한다.
