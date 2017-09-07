(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('body-log', {
            parent: 'entity',
            url: '/body-log?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BodyLogs'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/body-log/body-logs.html',
                    controller: 'BodyLogController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('body-log-detail', {
            parent: 'body-log',
            url: '/body-log/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BodyLog'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/body-log/body-log-detail.html',
                    controller: 'BodyLogDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'BodyLog', function($stateParams, BodyLog) {
                    return BodyLog.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'body-log',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('body-log-detail.edit', {
            parent: 'body-log-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/body-log/body-log-dialog.html',
                    controller: 'BodyLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BodyLog', function(BodyLog) {
                            return BodyLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('body-log.new', {
            parent: 'body-log',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/body-log/body-log-dialog.html',
                    controller: 'BodyLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                photo: null,
                                photoContentType: null,
                                bodyLogDatetime: null,
                                updateId: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('body-log', null, { reload: 'body-log' });
                }, function() {
                    $state.go('body-log');
                });
            }]
        })
        .state('body-log.edit', {
            parent: 'body-log',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/body-log/body-log-dialog.html',
                    controller: 'BodyLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BodyLog', function(BodyLog) {
                            return BodyLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('body-log', null, { reload: 'body-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('body-log.delete', {
            parent: 'body-log',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/body-log/body-log-delete-dialog.html',
                    controller: 'BodyLogDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BodyLog', function(BodyLog) {
                            return BodyLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('body-log', null, { reload: 'body-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
