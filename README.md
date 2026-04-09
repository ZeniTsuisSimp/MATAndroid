# MDT - Mobile Diagnostic Tool for Android

This repository contains a fresh Android implementation of MDT using Jetpack Compose and Material 3.

## Included in this first version

- Part 1: a responsive UI with progressive disclosure and a compact dashboard
- Part 2: real device, battery, software, hardware, and network details from Android APIs
- Part 3: grouped sensor availability checks for motion, position, and environment sensors
- Part 4: internal/external storage summary, permission-gated call log inspection, and report generation/sharing

## Notes

- IMEI/MEID and richer network details require `READ_PHONE_STATE`.
- Call log access requires `READ_CALL_LOG`.
- Camera, sound, and display are represented as readiness checks in this version; full active hardware test flows can be added next.
