## Race Condition
: 두 개 이상의 Thread(혹은 프로세스)가 한꺼번에 공유자원에 접근하려 경쟁하는 상태를 말한다.

## MySQL에서 동시성 이슈를 위해 제공하는 기능
#### 1. Pessimistic Lock(비관적 잠금)
: 실제로 data에 lock을 걸어서 정합성을 맞춘다.
#### 2. Optimistic Lock(낙관적 잠금)
엔티티에 version(@Version)을 추가해서 db를 select 할때 마다 version을 +1씩 update시 버전을  하면서 정합성을 맞춘다.
- Pessimistic Lock 과 다르게 별도로 lock을 걸지 않아 성능에서는 더 좋다.
- 하지만 update 실패시, 재시도 로직(while문)을 개발자가 작성해야한다.
- 충돌이 자주 일어난다면 pessimistic, 자주 일어나지 않는다면 optimistic 이 좋다.

#### 3. Named Lock
Pessimisic Lock 과 동일하나 Pessimisic Lock은 row와 컬럼 단위로 lock을 건다면 Named Lock은 metadata 단위이다.<br>
Transactional 이 끝난다고 해서 알아서 lock이 해제되지 않기때문에 따로 해제하거나 선점시간이 지나야 해제된다.
- NamedLock은 커넥션을 2개 사용하기 때문에 실제로 사용할때 꼭 데이터 소스를 분리해야한다 
  - Lock 획득을 위해 1개의 커넥션 + @Transactional을 위한 커넥션 1개
  - 분산락을 구현할때 사용한다.
    - 분산락이란 여러서버에서 공유된 데이터를 제어하기 위해 사용하는 기술입니다.
