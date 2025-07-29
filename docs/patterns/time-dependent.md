---
title: Time-dependent
tags:
  - Testing
  - Open/Closed
  - Liskov substitution
  - Dependency inversion
---

![](../img/clock-1274699_1280.jpg)

> Dependency injection doesn't end `now()`.

There are certain constructs and expressions that immediately raise a red flag, whenever I see them:

 * `now()` within `Instant`, `OffsetDateTime`, etc.
 * `new Date()` or `Calendar.getInstance()` (also because they are deprecated)
 * `current_date`, `current_time` and `current_timestamp`

Pretty much everything that uses the system clock.
My main concern is that it makes the code hard to test, slow to test or both.

Let's say I have this campaign class.
A campaign has a start and end date.
Now I'd like to have an easy way to tell whether it's currently active:

```kotlin
class Campaign(private val start: Instant, private val end: Instant) {

    fun isActive(): Boolean {
        val now = Instant.now()
        return start <= now && now <= end
    }

}
```

The code is relatively easy to understand, but it's almost impossible to test, unless I:

 * Mock the static `Instant.now()` method
 * Change the actual system clock for the test
 * Produce dynamic start and end dates, depending on the current time

Neither of those options is ideal.
I'd rather have a test that:

 * Works only with constants
 * Is independent of the system's clock it's running on
 * Is fast, i.e. not waiting or sleeping

```kotlin
@Test
fun `campaign is active`() {
    val start = Instant.parse("2020-12-13T11:00:00.00Z")
    val end = Instant.parse("2020-12-25T11:00:00.00Z")
    val unit = Campaign(start, end)

    assertTrue(unit.isActive())
}
```

In order to make that pass, I'd change the code and test to:

```kotlin
fun isActive(now: Instant): Boolean {
    return start <= now && now <= end
}

val now = Instant.parse("2020-12-19T09:32:17.00Z")
assertTrue(unit.isActive(now))
```

I can now:

 * Have as many of those tests as I want.
 * Properly test the boundaries without running into race conditions.
 * Check whether a campaign was active in the past or will be active in the future, just by passing in a different reference timestamp.

## Clock

There are cases where I can't or don't want to pass in an `Instant` directly.
The next best thing is to introduce an indirect dependency onto a `Clock`.
That clock is then passed in as a constructor argument.

That dependency can be replaced by a fixed clock or a fake clock during tests.
In production, it would then be `Clock.systemUTC()`.

```xml
<dependency>
  <groupId>com.mercateo</groupId>
  <artifactId>test-clock</artifactId>
  <version>1.0.2</version>
  <scope>test</scope>
</dependency>
```

## Spring Scheduling

Passing a clock explicitly as a dependency also works with the latest version of Spring Scheduling:

```kotlin
@EnableScheduling
class SchedulingConfiguration {

    @Bean
    fun taskScheduler(clock: Clock): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.clock = clock
        scheduler.poolSize = 5
        scheduler.threadNamePrefix = "ThreadPoolTaskScheduler"
        return scheduler
    }
}
```

## Database

Eliminating time dependency is not limited to traditional programming languages.
Especially within the database queries, e.g. SQL, it applies in exactly the same way.

Assuming I have this query to find *active* logins (not older than 5 minutes):

```sql
SELECT COUNT(1)
  FROM logins
 WHERE AGE(created_at, current_timestamp) < '5 minutes'::INTERVAL 
```

The query is pretty straightforward to write, but it's very hard to test.
I can either execute an additional statement, exclusive to my test:

```kotlin
@Test
fun `only finds logins that are five minutes old or less`() {
    val login = login()
    execute("UPDATE logins SET created_at = created_at - '5 Minutes'::INTERVAL WHERE id = ${login.id}")
    assertThat(findLoginsLastFiveMinutes()).isEmpty()
}
```

Or I can actually wait for 5 minutes. (Don't do that!)

```kotlin
@Test
fun `only finds logins that are five minutes old or less`() {
    val login = login()
    Thread.sleep(Duration.ofMinutes(5).toMillis())
    assertThat(findLoginsLastFiveMinutes()).isEmpty()
}
```

If I rewrite the statement instead and depend on *now* being passed in:

```sql
SELECT COUNT(1)
  FROM logins
 WHERE AGE(created_at, :now) < '5 minutes'::INTERVAL 
```

Then I can change my test to:

```kotlin
val clock = TestClock()
val unit = UserRepository(clock)

@Test
fun `only finds logins that are five minutes old or less`() {
    val login = login()
    clock.fastForward(Duration.ofMinutes(5))
    assertThat(findLoginsLastFiveMinutes()).isEmpty()
}
```

The benefits of doing that are huge:

 * Test no longer needs to execute a custom SQL statement
 * Test is not sleeping/waiting/idling, i.e. it's fast
 * Production code is **more powerful** than before:
   I can now find *active* logins at an arbitrary point in time, if I want to.

## References

* [Test Clock](https://github.com/Mercateo/test-clock), a great small library to have a mutable `Clock` implementation
