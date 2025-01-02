# Manual for the Command Line Application

As a project idea I created a simple CLI application using **Clikt** which can be used to manage New Year's resolutions.

Following command needs to executed in the root folder of the Intellij project:
```
> .\app resolution list
```

When the command is called the first time the `installDist` gradle task is executed which adds an `install` folder inside
the `build` folder. This is the location where the application is installed using gradle. When calling the application again
this step is skipped.

By appending `--help` to the individual commands a help text for the command usage is shown.

The CLI application contains following commands:

## Commands

### List New New Year's resolutions
Command: `resolution list`

```
> .\app resolution list --help

Usage: resolution list [<options>]

  Shows a list of all added New Year's resolutions.

  This will show a list of New Year's resolutions which can be numbered and ordered by their priority.

Options:
  -n, --numbered           Numbers the New Year's resolutions according to their insertion order starting from 1.
  -o, --orderedByPriority  Orders the New Year's resolutions by their priority.
  -h, --help               Show this message and exit
```

### Create New Year's resolution
Command: `resolution create`

```
> .\app resolution create --help

Usage: resolution create [<options>] <text>

  Creates a New Year's resolution.

  This will create a New Year's resolution and adds it to the existing ones.

Options:
  -p, --priority=<int>   Priority of the New Year's resolution. Must be between 1 and 10.
  -d, --deadline=<text>  Sets a deadline in the yyyy-MM-dd format for the New Year's resolution.
  -h, --help             Show this message and exit

Arguments:
  <text>  Description of the New Year's resolution.
```

### Edit New Year's resolution
Command: `resolution edit`

```
> .\app resolution edit --help

Usage: resolution edit [<options>] <position> <command> [<args>]...

  Updates a New Year's resolution.

  This will update an existing New Year's resolution by editing properties as well as deleting optional ones.

Options:
  -t, --text=<text>      Description of the New Year's resolution.
  -p, --priority=<int>   Priority of the New Year's resolution.
  -d, --deadline=<text>  Sets a deadline for the New Year's resolution.
  -h, --help             Show this message and exit

Arguments:
  <position>  Position of the New Year's resolution in the list.

Commands:
  remove  Removes optional properties of a New Year's resolution.
```

### Delete New Year's resolution
Command: `resolution delete`

```
> .\app resolution delete --help

Usage: resolution delete [<options>] <position>

  Deletes an existing New Year's resolution.

  This will delete an existing New Year's resolution by specifying it's position.

Options:
  -h, --help  Show this message and exit

Arguments:
  <position>  Position of the New Year's resolution in the list.
```

## Sample Command to Create a New Year's Resolution
```
> .\app resolution create "My first New Year's resolution" --priority 9 --deadline 2025-04-25
```
