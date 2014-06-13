// Generated on 2013-11-09 using generator-webapp 0.4.3
'use strict';

module.exports = function (grunt) {
    // show elapsed time at the end
    require('time-grunt')(grunt);
    // load all grunt tasks
    require('load-grunt-tasks')(grunt);

    grunt.initConfig({
        copy: {
            classic_mirage_style: {
                files: [
                    {
                        src: ['styles/_main_classic_mirage_style.scss'],
                        dest: 'styles/main.scss'
                    }
                ]
            },
            bootstrap_style: {
                files: [
                    {
                        src: ['styles/_main_bootstrap_style.scss'],
                        dest: 'styles/main.scss'
                    }
                ]
            }
        },
        compass: {
            prod: {
                options: {
                    config: 'config-prod.rb'
                }

            },
            dev: {
                options: {
                    config: 'config-dev.rb'
                }

            }
        },
        coffee: {
            glob_to_multiple: {
                expand: true,
                flatten: true,
                cwd: 'scripts',
                src: ['*.coffee'],
                dest: 'scripts',
                ext: '.js'
            }
        },
        handlebars: {
            compile: {
                options: {
                    namespace: "DSpace.templates",
                    processName: function(filePath) {
                        return filePath.replace(/^templates\//, '').replace(/\.handlebars$/, '');
                    }
                },
                files: {
                    "scripts/templates.js": ["templates/*.handlebars"]
                }
            }
        },
        useminPrepare:{
            prod: {
                src: ['scripts.xml'],
                options: {
                    // fool usemin in to putting theme.js straight into the scripts
                    // folder, and not in a separate dist folder. And no, you can't
                    // just use an empty string, I tried ;)
                    dest: 'dist/../'
                }
            },
            dev: {
                src: ['scripts.xml'],
                options: {
                    // same deal
                    dest: 'dist/../'
                }
            }
        } ,
        usemin: {
            html:'scripts.xml'
        }
    });


    grunt.registerTask('classic_mirage_style', [
        'copy:classic_mirage_style'
    ]);
    grunt.registerTask('bootstrap_style', [
        'copy:bootstrap_style'
    ]);
    grunt.registerTask('no-compass-prod', [
        'coffee', 'handlebars', 'useminPrepare:prod','concat','uglify','usemin'
    ]);
    grunt.registerTask('no-compass-dev', [
        'coffee', 'handlebars', 'useminPrepare:dev','concat','uglify','usemin'
    ]);
    grunt.registerTask('prod', [
        'compass:prod', 'no-compass-prod'
    ]);
    grunt.registerTask('dev', [
        'compass:dev', 'no-compass-dev'
    ]);
    grunt.registerTask('default', [
        'classic_mirage_style',
        'prod'
    ]);
};