(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('weight', {
            parent: 'entity',
            url: '/weight',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Weights'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/weight/weights.html',
                    controller: 'WeightController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('weight-detail', {
            parent: 'weight',
            url: '/weight/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Weight'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/weight/weight-detail.html',
                    controller: 'WeightDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Weight', function($stateParams, Weight) {
                    return Weight.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'weight',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('weight-detail.edit', {
            parent: 'weight-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/weight/weight-dialog.html',
                    controller: 'WeightDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Weight', function(Weight) {
                            return Weight.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('weight.new', {
            parent: 'weight',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/weight/weight-dialog.html',
                    controller: 'WeightDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                value: null,
                                weightDateTime: null,
                                comment: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('weight', null, { reload: 'weight' });
                }, function() {
                    $state.go('weight');
                });
            }]
        })
        .state('weight.edit', {
            parent: 'weight',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/weight/weight-dialog.html',
                    controller: 'WeightDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Weight', function(Weight) {
                            return Weight.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('weight', null, { reload: 'weight' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('weight.delete', {
            parent: 'weight',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/weight/weight-delete-dialog.html',
                    controller: 'WeightDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Weight', function(Weight) {
                            return Weight.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('weight', null, { reload: 'weight' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
