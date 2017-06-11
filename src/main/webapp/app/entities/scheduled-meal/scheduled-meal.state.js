(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('scheduled-meal', {
            parent: 'entity',
            url: '/scheduled-meal',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ScheduledMeals'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meals.html',
                    controller: 'ScheduledMealController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('scheduled-meal-detail', {
            parent: 'scheduled-meal',
            url: '/scheduled-meal/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ScheduledMeal'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meal-detail.html',
                    controller: 'ScheduledMealDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ScheduledMeal', function($stateParams, ScheduledMeal) {
                    return ScheduledMeal.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'scheduled-meal',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('scheduled-meal-detail.edit', {
            parent: 'scheduled-meal-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meal-dialog.html',
                    controller: 'ScheduledMealDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ScheduledMeal', function(ScheduledMeal) {
                            return ScheduledMeal.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('scheduled-meal.new', {
            parent: 'scheduled-meal',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meal-dialog.html',
                    controller: 'ScheduledMealDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                targetTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('scheduled-meal', null, { reload: 'scheduled-meal' });
                }, function() {
                    $state.go('scheduled-meal');
                });
            }]
        })
        .state('scheduled-meal.edit', {
            parent: 'scheduled-meal',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meal-dialog.html',
                    controller: 'ScheduledMealDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ScheduledMeal', function(ScheduledMeal) {
                            return ScheduledMeal.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('scheduled-meal', null, { reload: 'scheduled-meal' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('scheduled-meal.delete', {
            parent: 'scheduled-meal',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/scheduled-meal/scheduled-meal-delete-dialog.html',
                    controller: 'ScheduledMealDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ScheduledMeal', function(ScheduledMeal) {
                            return ScheduledMeal.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('scheduled-meal', null, { reload: 'scheduled-meal' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
