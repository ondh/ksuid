# ksuid
A Kotlin implementation of K-Sortable Globally Unique IDs.
For more information,you can visit [Segment's KSUID library](https://github.com/segmentio/ksuid) and [Segment's Blog](https://segment.com/blog/a-brief-history-of-the-uuid/).

# Quick Start
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