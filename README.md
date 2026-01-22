# Hytale Mods Collection

## Overview

This repository contains a collection of Hytale server mods created by me as part of a learning and experimentation process with the Hytale modding architecture.
These projects were developed to explore core concepts such as configuration handling, server-side systems, and mod interoperability.
While they are functional and usable, they are not actively maintained and should be considered reference or learning resources.

## Included Mods

### KatsiLib

A shared utility and framework library used by other mods in this collection.

#### Features:

 * Centralized JSON configuration loading and saving

 * Common utilities and helpers for mod development

 * Designed to reduce duplication across mods

### RewardBoxes

A configurable loot-crate system that allows servers to reward players with randomized digital items.

#### Features:

 * Customizable reward boxes (loot crates)

 * Key-based opening system

 * Flexible reward definitions

 * Designed for integration with other mods

### DailyLoginRewards

A daily login reward system that incentivizes player retention.

#### Features:

 * Configurable daily reward schedules

 * Supports streak-based rewards

 * Optional integration with RewardBoxes for loot-based rewards

 * Server-controlled balancing and customization

## Repository Structure

Each mod is maintained in its own directory within this repository. While the mods are developed together, they are designed to remain logically independent.

Shared functionality is centralized within KatsiLib.

## Usage

Each mod includes its own configuration files and setup instructions. Refer to the individual mod directories for detailed documentation and configuration examples.
