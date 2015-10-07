# JetBrains dotTrace plugin for TeamCity #

This plugin provides ability to run build step under the [JetBrains dotTrace Command-Line Profiler](https://www.jetbrains.com/profiler/help/Performance_Profiling__Profiling_Using_the_Command_Line.html) for the .Net related build runners in the TeamCity.

## Install ##

To install the plugin, put [zip archive](http://teamcity.jetbrains.com/httpAuth/app/rest/builds/buildType:TeamCityPluginsByJetBrains_DotTrace_Build,pinned:true,status:SUCCESS,branch:master/artifacts/content/dotTrace.zip) to 'plugins' direrctory under TeamCity data directory. Restart the server. JetBrains dotTrace ñommand-line profiler is a free separate tool that contains a self-profiling API. To use JetBrains dotTrace ñommand-line profiler on the TeamCity you should have the JetBrains dotTrace ñommand-line profiler on the each TeamCity agent where you are going to run them.

## Implemention ##

This project contains 3 modules: 'dotTrace-server', 'dotTrace-agent' and 'dotTrace-common'. They contain code for server and agent parts and a common part, available for both (agent and server). When implementing components for server and agent parts, do not forget to update spring context files under 'main/resources/META-INF'. See [TeamCity documentation](https://confluence.jetbrains.com/display/TCD9/Developing+Plugins+Using+Maven) for details on plugin development.

## Build ##

Use 'mvn package' command from the root project to build your plugin. Resulting package 'dotTrace.zip' will be placed in 'target' directory. The build is configured on the [JetBrains TeamCity build server](https://teamcity.jetbrains.com/viewLog.html?buildTypeId=TeamCityPluginsByJetBrains_DotTrace_Build&buildId=lastPinned&buildBranch=%3Cdefault%3E).

## License ##

JetBrains dotTrace plugin for TeamCity is under the [Apache License](https://github.com/JetBrains/teamcity-dottrace/blob/master/LICENSE).

## Contributors ##

- [Nikolay Pianikov](https://github.com/NikolayPianikov)

## Resources ##

- [Working with dotTrace Command-Line Profiler](https://www.jetbrains.com/profiler/help/Performance_Profiling__Profiling_Using_the_Command_Line.html)
- [Download JetBrains dotTrace command line tools](https://www.nuget.org/packages/JetBrains.DotMemoryUnit/) see Related downloads, dotTrace command line tools
- [Using dotTrace Perfomance API](https://confluence.jetbrains.com/display/NETCOM/Using+dotTrace+Perfomance+API)
