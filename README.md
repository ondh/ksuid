# ksuid
A Kotlin implementation of K-Sortable Globally Unique IDs.
For more information,you can visit [Segment's KSUID library](https://github.com/segmentio/ksuid) and [Segment's Blog](https://segment.com/blog/a-brief-history-of-the-uuid/).

# Quick Start
## in java
``` java
final String uid = Ksuid.generate();
// e.g. output: Be785NYYxP29BJiAJPupfsXuGpR

final String decoded = Ksuid.parse(uid);
// e.g. output: Time: 2017-07-08T21:13:08Z[UTC]
//              Timestamp: 1499548388
//              Payload: [-42, 24, -60, -3, -66, 38, 32, 9, 62, -22, 95, -79, 123, -122, -91, 0] 
```

```kotlin
val uid = Ksuid().generate()
//GcQhZ5uVHCOuh2zWBDUXPP937NK

val decoded = Ksuid().parse(uid)

val timestamp = Ksuid().getTimestamp(uid)
```

# Thanks
[Base62 encode/decode by glowfall](https://github.com/glowfall/base62)

# Update Log
- 1.0.0: Initial public release