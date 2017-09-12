(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('jaca', {
            parent: 'entity',
            url: '/jaca',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Jacas'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/jaca/jacas.html',
                    controller: 'JacaController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('jaca-detail', {
            parent: 'jaca',
            url: '/jaca/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Jaca'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/jaca/jaca-detail.html',
                    controller: 'JacaDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Jaca', function($stateParams, Jaca) {
                    return Jaca.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'jaca',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('jaca-detail.edit', {
            parent: 'jaca-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jaca/jaca-dialog.html',
                    controller: 'JacaDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Jaca', function(Jaca) {
                            return Jaca.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('jaca.new', {
            parent: 'jaca',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jaca/jaca-dialog.html',
                    controller: 'JacaDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                jacaDateTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('jaca', null, { reload: 'jaca' });
                }, function() {
                    $state.go('jaca');
                });
            }]
        })
        .state('jaca.edit', {
            parent: 'jaca',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jaca/jaca-dialog.html',
                    controller: 'JacaDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Jaca', function(Jaca) {
                            return Jaca.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('jaca', null, { reload: 'jaca' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('jaca.delete', {
            parent: 'jaca',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/jaca/jaca-delete-dialog.html',
                    controller: 'JacaDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Jaca', function(Jaca) {
                            return Jaca.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('jaca', null, { reload: 'jaca' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
