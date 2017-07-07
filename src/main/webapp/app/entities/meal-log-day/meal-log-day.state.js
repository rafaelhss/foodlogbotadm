(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('meal-log-day', {
            parent: 'entity',
            url: '/meal-log-day',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MealLogDays'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/meal-log-day/meal-log-days-timeline.html',
                    controller: 'MealLogDayController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('meal-log-day-detail', {
            parent: 'meal-log-day',
            url: '/meal-log-day/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'MealLogDay'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/meal-log-day/meal-log-day-detail.html',
                    controller: 'MealLogDayDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'MealLogDay', function($stateParams, MealLogDay) {
                    return MealLogDay.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'meal-log-day',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('meal-log-day-detail.edit', {
            parent: 'meal-log-day-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log-day/meal-log-day-dialog.html',
                    controller: 'MealLogDayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MealLogDay', function(MealLogDay) {
                            return MealLogDay.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('meal-log-day.new', {
            parent: 'meal-log-day',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log-day/meal-log-day-dialog.html',
                    controller: 'MealLogDayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                mealLogDayDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('meal-log-day', null, { reload: 'meal-log-day' });
                }, function() {
                    $state.go('meal-log-day');
                });
            }]
        })
        .state('meal-log-day.edit', {
            parent: 'meal-log-day',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log-day/meal-log-day-dialog.html',
                    controller: 'MealLogDayDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MealLogDay', function(MealLogDay) {
                            return MealLogDay.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('meal-log-day', null, { reload: 'meal-log-day' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('meal-log-day.delete', {
            parent: 'meal-log-day',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/meal-log-day/meal-log-day-delete-dialog.html',
                    controller: 'MealLogDayDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MealLogDay', function(MealLogDay) {
                            return MealLogDay.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('meal-log-day', null, { reload: 'meal-log-day' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
