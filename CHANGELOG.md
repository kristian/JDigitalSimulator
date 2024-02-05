## 2.3.0 (2024-02-05)
### Fixed
  - Ability to open files from old JDigitalSimulator versions (before open-sourcing, version < 1.*), see the README.md instructions

## 2.2.0 (2022-06-18)
### Added
  - US / ANSI style gate symbols and option to switch in the menu, thanks to Sett17

## 2.1.0 (2022-01-25)
### Fixed
  - Bumped launch4j to version 2.1.0

## 2.0.3 (2020-03-04)
### Fixed
  - Fixed the (very buggy) 74181 ALU, thanks to Frank Möbius

## 2.0.2 (2017-05-31)
### Added
  - Added support to load JDigitalSimulator simulations stored with version 1.*

## 2.0.1 (2017-03-16)
### Fixed
  - Fixed high cpu-usage while dragging components with grid on

## 2.0.0 (2017-03-14)
### Added
  - This nicely formatted CAHNGELOG.md!

### Changed
  - JDigitalSimulator is now open source! Find the source code on GitHub at https://github.com/kristian/JDigitalSimulator

## 1.1.7 (2012-12-12, 9.509 lines, 36.133 words)
### Changed
  - External simulation can now be located at the simulation file location (relative)

## 1.1.6 (2012-11-30, 9.483 lines, 36.060 words)
### Added
  - New plugin component catalog at http://kra.lc/projects/jdigitalsimulator/catalog.html

### Fixed
  - Fixed mouse interaction with components
  - 74F181 ALU removed (buggy), working DIN 74181 ALU added

## 1.1.5 (2012-11-28, 9.475 lines, 35.963 words)
### Fixed
  - ALU is a 74F181 (Philips N74F181N), not a 74181

## 1.1.4 (2012-11-15, 9.475 lines, 35.963 words)
### Added
  - Added dialog to prevent unsaved changes to be lost

### Fixed
  - Fixed bug in RAM component-Address calculation fixed

## 1.1.3 (2012-11-15, 9.463 lines, 35.915 words)
### Fixed
  - Fixed bug in ALU, which prevented to load stored circuits

## 1.1.2 (2012-10-30, 9.456 lines, 35.899 words)
### Fixed
  - Fixed typo in the about dialog
  - Further tweaked performance on drag & drop of components
  - Moved paintWires from ContactList class to ContactUtilities, please adapt your code

## 1.1.1 (2012-10-27, 9.487 lines, 35.894 words)
### Fixed
  - Fixed bug on improved drag & drop, wire handling fixed
  - Tweaked performance on drag & drop a bit

## 1.1.0 (2012-10-27, 9.456 lines, 35.749 words)
### Changed
  - Higher granularity to zoom
  - Highly improved performance to copy & paste also large amounts of components
  - Highly improved performance to drag & drop also large amounts of components
  - Improved copy & paste handling for do/undo history
  - Added ContactUtilities class, please adapt your code for concatenateContacts

## 1.0.14 (2012-10-21, 9.220 lines, 34.992 words)
### Added
  - Added memory components (1 to 8-Bit Memory, 1 to 8-Bit Guarded Memory, 1 to 1024-Bit RAM)

### Fixed
  - Copy & Paste fixed for plugin components

## 1.0.13 (2012-10-20, 8.887 lines, 33.904 words)
### Added
  - Added an ALU 74181 component

### Fixed
  - Copy & Paste is now working again
  - Fixed junctions so they can be moved/removed again.

## 1.0.12 (2012-06-15, 8.696 lines, 33.145 words)
### Changed
  - Fixed accidentally dragging a component by adding a small threshold
  - Window size / position now gets stored
  - Better use of window space / resizing works better now

## 1.0.11 (2012-02-13, 8.664 lines, 32.978 words)
### Changed
  - Faster scrolling of the component catalog using the mouse wheel
  - Contacts can be selected more precisely now
  - Wires are now drawn above contacts
  - Only numbered input are allowed for number textboxes now

### Fixed
  - Wires have not been removed when changing component preferences
  - Can not load stored simulations using components added by plugins
  - Clicks with the right mouse button on a component are not forwarded to the component anymore

## 1.0.10 (2012-02-01, 8.611 lines, 32.744 words)
### Changed
  - Wires can now be removed by selecting them, and pressing the DEL key, as components

### Fixed
  - Shift register configuration was not working properly

## 1.0.9 (2012-01-21, 8.611 lines, 32.744 words)
### Added
  - Added an undo/redo function (caution: does not work for wires at the moment)

### Changed
  - Added icons to the menubar

### Fixed
  - Fixed some minor bugs with copy & pasting

## 1.0.8 (2011-04-26, 8.459 lines, 32.175 words)
### Fixed
  - The half-adder was not working as expected

## 1.0.7 (2010-09-23, 8.459 lines, 32.175 words)
### Changed
  - Added possibility to put a JDigitalSimulator.properties file to the application path

## 1.0.6 (2010-08-24, 8.444 lines, 32.132 words)
### Changed
  - Added localization to build-in components
  - Added possibility to predefine flip-flop configuration
  - Made the voltmeter display smaller
  - File dialogs are now pointing to the lastly opened directory by default
  - Added wires from the components to their contacts
  - Translated even the component names

### Fixed
  - Fixed bug with to long voltmeter/external descriptions
  - Fixed bug with display array, contacts are no displayed correctly

## 1.0.5 (2010-08-01, 7.977 lines, 30.682 words)
### Added
  - Added Register, Comparator and External-Components
  - Added "snap-in" effect when dragging wires around
  - Added possibility to save oscilloscope as image
  - Added possibility to turn draw-direction of oscilloscope around

### Changed
  - Changed behavior of wires connected to junctions slightly
  - Changed appearance of gates slightly

### Fixed
  - Fixed clone-bug of component

## 1.0.4 (2010-07-27, 7.031 lines, 27.905 words)
### Changed
  - Enhanced the possibility to move wires around

### Fixed
  - Fixed date bug in this change file ;-)
  - Fixed bug while moving multiple components

## 1.0.3 (2010-07-26, 6.993 lines, 27.782 words)
### Added
  - Added possibility to change default look and feel
  - Added possibility to move wires around

### Changed
  - Changed serialization procedure (only models get serialized)

### Fixed
  - Fixed the serialization bug of all synth look and feels

## 1.0.2 (2010-07-26, 6.911 lines, 27.520 words)
### Added
  - Added this changelog file to default build package
  - Added button to hide preview images of the components
  - Added binary counter and loadable binary counter to build-in components
  - Changed wire-painting algorithm, a wire is now one polyline
  - All wires are now selectable and may have a popup menu
  - Added junction component to separate wires (by right click)
  - Added snap-to-grid functionality if grid is enabled
  - Added popup menu to wires to add junctions and voltmeters

### Changed
  - Changed simulation behavior to correct wrong simulation of sequential circuits
  - Changed simulation speed from one 10th of a second to one 100th
  - Changed simulation repaint from one 10th of a second to one 50th
  - Changed behavior of preferences dialog for all components
  - Improved performance of components by buffering contacts and locations
  - Changed version number of all build-in components, incompatible changes

### Fixed
  - Fixed bug with flip-flops, all flip-flops are now edge triggered by default
  - Generally fixed bugs while skipping through the whole source code again

## 1.0.1 (2010-07-23, 6.353 lines, 25.948 words)
### Added
  - Oscilloscope is now working
  - Added examples

### Changed
  - Changed alternative LookAndFeel

### Fixed
  - Fixed Linux Bug in alternative classloader
  - Fixed public class problem while serializing

## 1.0.0 (2010-07-22, 5.957 lines, 21.500 words)
### Added
  - Ported the whole Digital Simulator to Java
  - Simulation, Components, Oscilloscope, Printable
  - Launched JDigitalSimulation homepage: http://kra.lc/projects/jdigitalsimulator
  - Check homepage for more details of the first version