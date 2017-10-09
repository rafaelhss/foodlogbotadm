(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('meal-log', {
            parent: 'entity',
            url: '/meal-log',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MealLogs'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/meal-log/meal-logs.html',
                    controller: 'MealLogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('meal-log-detail', {
            parent: 'meal-log',
            url: '/meal-log/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MealLog'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/meal-log/meal-log-detail.html',
                    controller: 'MealLogDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'MealLog', function($stateParams, MealLog) {
                    return MealLog.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'meal-log',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('meal-log-detail.edit', {
            parent: 'meal-log-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log/meal-log-dialog.html',
                    controller: 'MealLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MealLog', function(MealLog) {
                            return MealLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('meal-log.new', {
            parent: 'meal-log',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log/meal-log-dialog.html',
                    controller: 'MealLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                photo: null,
                                photoContentType: null,
                                mealDateTime: null,
                                comment: null,
                                updateId: null,
                                rating: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('meal-log', null, { reload: 'meal-log' });
                }, function() {
                    $state.go('meal-log');
                });
            }]
        })
        .state('meal-log.edit', {
            parent: 'meal-log',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log/meal-log-dialog.html',
                    controller: 'MealLogDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MealLog', function(MealLog) {
                            return MealLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('meal-log', null, { reload: 'meal-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('meal-log.delete', {
            parent: 'meal-log',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log/meal-log-delete-dialog.html',
                    controller: 'MealLogDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MealLog', function(MealLog) {
                            return MealLog.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('meal-log', null, { reload: 'meal-log' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
